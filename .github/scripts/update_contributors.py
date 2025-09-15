#!/usr/bin/env python3
import json, os, sys, time, urllib.request, urllib.error

OWNER_REPO = "TeamGalacticraft/Galacticraft"
MOD_JSON_PATH = "src/main/resources/fabric.mod.json"
ETAG_PATH = "build/contributors.etag"

API_BASE = f"https://api.github.com/repos/{OWNER_REPO}"

def _countdown_30s():
    # 30 -> 20 -> 10 -> polling...
    for t in (30, 20, 10):
        print(f"Polling in {t}s...")
        time.sleep(10)
    print("Polling...")

def _http_get(url, token=None, add_if_none_match: str | None = None):
    req = urllib.request.Request(url)
    req.add_header("Accept", "application/vnd.github+json")
    if token:
        req.add_header("Authorization", f"Bearer {token}")
    if add_if_none_match:
        req.add_header("If-None-Match", add_if_none_match)
    resp = urllib.request.urlopen(req, timeout=30)
    body = resp.read().decode("utf-8") or "[]"
    return resp.status, json.loads(body), resp.headers

def fetch_stats_with_poll():
    """
    Poll /stats/contributors until GitHub returns non-empty data.
    Treats 202 or [] as 'warm-up not finished'. Never falls back.
    Returns (raw_list, latest_etag).
    """
    token = (os.environ.get("GITHUB_TOKEN") or "").strip() or None
    url = f"{API_BASE}/stats/contributors?per_page=100"
    latest_etag = None

    # Important: DO NOT send If-None-Match while warming up,
    # or GitHub may 304 and you won't see the new data when it’s ready.
    while True:
        try:
            status, data, headers = _http_get(url, token=token, add_if_none_match=None)
            if latest_etag is None:
                latest_etag = headers.get("ETag")
        except urllib.error.HTTPError as e:
            # Only 4xx/5xx come here; keep retrying on transient errors
            print(f"HTTP {e.code} from GitHub; will retry. Reason: {e.reason}")
            _countdown_30s()
            continue
        except urllib.error.URLError as e:
            print(f"Network error contacting GitHub; will retry. Reason: {e.reason}")
            _countdown_30s()
            continue

        # 202 = warm-up in progress
        if status == 202:
            print("Warm-up not finished (202).")
            _countdown_30s()
            continue

        # Status 200, but GitHub may still return [] while stats are being generated
        if isinstance(data, list) and len(data) == 0:
            print("Warm-up not finished (empty stats array).")
            _countdown_30s()
            continue

        # Got data!
        print("Warm-up finished. Data found.")
        # Normalize to your build_payload() shape: {author:{...}, total:int}
        raw = [{"author": (c.get("author") or {}), "total": c.get("total", 0) or 0} for c in data]
        return raw, latest_etag

def build_payload(raw):
    # filter bots & sort by contributions desc; guard against author == None
    filtered = []
    for c in raw:
        author = c.get("author") or {}
        login = (author.get("login") or "")
        atype = (author.get("type") or "")
        if "bot" in atype.lower() or "bot" in login.lower():
            continue
        filtered.append({
            **author,
            "contributions": c.get("total", 0) or 0
        })

    filtered.sort(key=lambda c: c.get("contributions", 0), reverse=True)

    return [
        {
            "name": c.get("login", "") or "",
            "contact": {"homepage": c.get("html_url", "") or ""},
        }
        for c in filtered
    ]

def read_existing(root):
    arr = root.get("contributors")
    if not isinstance(arr, list):
        return []
    out = []
    for el in arr:
        if isinstance(el, dict):
            name = el.get("name", "") or ""
            homepage = ((el.get("contact") or {}).get("homepage", "") or "")
            out.append({"name": name, "contact": {"homepage": homepage}})
    return out

def write_etag(new_etag: str | None):
    if not new_etag:
        return
    os.makedirs(os.path.dirname(ETAG_PATH), exist_ok=True)
    with open(ETAG_PATH, "w", encoding="utf-8") as f:
        f.write(new_etag)

def main():
    # ensure fabric.mod.json exists
    if not os.path.exists(MOD_JSON_PATH):
        print(f"fabric.mod.json not found at {MOD_JSON_PATH}", file=sys.stderr)
        sys.exit(1)

    with open(MOD_JSON_PATH, "r", encoding="utf-8") as f:
        root = json.load(f)

    existing = read_existing(root)

    # Poll until stats are ready and non-empty
    try:
        raw, new_etag = fetch_stats_with_poll()
    except KeyboardInterrupt:
        print("Interrupted. Keeping existing contributors.")
        return
    except Exception as e:
        print(f"Failed to reach GitHub: {e}. Keeping existing contributors.")
        return

    new_contrib = build_payload(raw)

    # Persist the new ETag (if any)
    write_etag(new_etag)

    if existing == new_contrib:
        print("Contributors already up-to-date. No changes written.")
        return

    root["contributors"] = new_contrib
    with open(MOD_JSON_PATH, "w", encoding="utf-8") as f:
        json.dump(root, f, indent=2, ensure_ascii=False)
        f.write("\n")

    print(f"Updated contributors in fabric.mod.json ({len(new_contrib)})")

if __name__ == "__main__":
    main()