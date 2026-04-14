"""Move house-targeting jigsaw blocks in street NBTs from y=1 to y=0."""
import nbtlib
from pathlib import Path

BASE = Path(r"C:\Users\Gergo\Documents\Galacticraft-main\Galacticraft-main\src\main\resources\data\galacticraft\structure\village\moon\highlands\streets")

def fix_street(filepath):
    try:
        nbt = nbtlib.load(filepath, gzipped=True)
    except:
        nbt = nbtlib.load(filepath, gzipped=False)

    blocks = nbt.get("blocks", [])
    palette = nbt.get("palette", [])

    palette_names = {}
    for i, entry in enumerate(palette):
        palette_names[i] = str(entry.get("Name", ""))

    changed = 0
    for block in blocks:
        state_idx = int(block.get("state", -1))
        block_name = palette_names.get(state_idx, "")

        if "jigsaw" in block_name.lower():
            nbt_data = block.get("nbt", {})
            pool = str(nbt_data.get("pool", ""))
            pos = block.get("pos", [])

            if "house" in pool.lower() and len(pos) >= 3 and int(pos[1]) == 1:
                old_y = int(pos[1])
                pos[1] = nbtlib.Int(0)
                changed += 1
                print(f"  Moved jigsaw at ({pos[0]},{old_y},{pos[2]}) -> ({pos[0]},0,{pos[2]}) [pool={pool}]")

    if changed > 0:
        nbt.save(filepath, gzipped=True)
        print(f"  Saved {changed} changes")
    else:
        print(f"  No changes needed")

    return changed

total = 0
for nbt_file in sorted(BASE.glob("*.nbt")):
    print(f"\n--- {nbt_file.name} ---")
    total += fix_street(nbt_file)

print(f"\nTotal jigsaws moved: {total}")
