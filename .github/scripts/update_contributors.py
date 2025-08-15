#!/usr/bin/env python3
import json, os, sys, urllib.request, urllib.error

OWNER_REPO = "TeamGalacticraft/Galacticraft"
MOD_JSON_PATH = "src/main/resources/fabric.mod.json"
ETAG_PATH = "build/contributors.etag"


def http_get_contributors(use_etag=True):
    results = []
    token = os.environ.get("GITHUB_TOKEN", "").strip() or None
    url = f"https://api.github.com/repos/{OWNER_REPO}/contributors?per_page=100"
    latest_etag = None
    first = True

    while url:
        req = urllib.request.Request(url)
        req.add_header("Accept", "application/vnd.github+json")
        if token:
            req.add_header("Authorization", f"Bearer {token}")
        if use_etag and first and os.path.exists(ETAG_PATH):
            with open(ETAG_PATH, "r", encoding="utf-8") as f:
                req.add_header("If-None-Match", f.read().strip())

        try:
            with urllib.request.urlopen(req, timeout=15) as resp:
                if latest_etag is None:
                    latest_etag = resp.headers.get("ETag")
                data = json.loads(resp.read().decode("utf-8"))
                results.extend(data)

                # pagination
                link = resp.headers.get("Link", "")
                next_url = None
                for part in link.split(","):
                    part = part.strip()
                    if part.endswith('rel="next"'):
                        next_url = part[part.find("<") + 1 : part.find(">")]
                        break
                url = next_url
        except urllib.error.HTTPError as e:
            if e.code == 304 and first and use_etag:
                return [], None  # unchanged
            raise
        finally:
            first = False

    return results, latest_etag


def build_payload(raw):
    # filter bots & sort by contributions desc
    filtered = [
        c
        for c in raw
        if not (
                "bot" in c.get("type", "").lower()
                or "bot" in c.get("login", "").lower()
        )
    ]
    filtered.sort(key=lambda c: c.get("contributions") or 0, reverse=True)

    return [
        {
            "name": c.get("login", ""),
            "contact": {"homepage": c.get("html_url", "")},
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
            name = el.get("name", "")
            homepage = el.get("contact", {}).get("homepage", "")
            out.append({"name": name, "contact": {"homepage": homepage}})
    return out


def write_etag(new_etag: str | None):
    if not new_etag:
        return
    os.makedirs(os.path.dirname(ETAG_PATH), exist_ok=True)
    with open(ETAG_PATH, "w", encoding="utf-8") as f:
        f.write(new_etag)


def main():
    # load fmj
    if not os.path.exists(MOD_JSON_PATH):
        print(f"fabric.mod.json not found at {MOD_JSON_PATH}", file=sys.stderr)
        sys.exit(1)

    with open(MOD_JSON_PATH, "r", encoding="utf-8") as f:
        root = json.load(f)

    existing = read_existing(root)

    try:
        raw, new_etag = http_get_contributors(use_etag=True)
    except Exception as e:
        print(f"Failed to reach GitHub: {e}. Keeping existing contributors.")
        return

    if not raw and new_etag is None:
        print("Contributors unchanged (ETag 304). Skipping update.")
        return

    new_contrib = build_payload(raw)

    # Persist the new ETag (if any) regardless of whether the list changed
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