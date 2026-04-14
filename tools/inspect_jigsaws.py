"""Inspect all jigsaw blocks in village structure NBTs."""
import os
import nbtlib
from pathlib import Path

BASE = Path(r"C:\Users\Gergo\Documents\Galacticraft-main\Galacticraft-main\src\main\resources\data\galacticraft\structure\village\moon\highlands")

FOLDERS = ["starts", "streets", "houses", "terminators", "decor", "decays", "villagers", "misc"]

def inspect_nbt(filepath):
    """Extract jigsaw block info from an NBT structure file."""
    try:
        nbt = nbtlib.load(filepath, gzipped=True)
    except Exception:
        try:
            nbt = nbtlib.load(filepath, gzipped=False)
        except Exception as e:
            return f"  ERROR reading: {e}"

    results = []
    
    # Get structure size
    if "size" in nbt:
        size = nbt["size"]
        results.append(f"  Size: {size[0]}x{size[1]}x{size[2]}")
    
    # Look for blocks with nbt data containing jigsaw info
    blocks = nbt.get("blocks", [])
    palette = nbt.get("palette", [])
    
    # Build palette lookup
    palette_names = {}
    for i, entry in enumerate(palette):
        name = str(entry.get("Name", ""))
        palette_names[i] = name
    
    jigsaw_count = 0
    for block in blocks:
        state_idx = int(block.get("state", -1))
        block_name = palette_names.get(state_idx, "")
        
        if "jigsaw" in block_name.lower():
            jigsaw_count += 1
            nbt_data = block.get("nbt", {})
            pool = str(nbt_data.get("pool", "N/A"))
            name = str(nbt_data.get("name", "N/A"))
            target = str(nbt_data.get("target", "N/A"))
            final_state = str(nbt_data.get("final_state", "N/A"))
            pos = block.get("pos", [])
            pos_str = f"({pos[0]},{pos[1]},{pos[2]})" if len(pos) >= 3 else "?"
            
            results.append(f"  Jigsaw @ {pos_str}:")
            results.append(f"    pool:        {pool}")
            results.append(f"    name:        {name}")
            results.append(f"    target:      {target}")
            results.append(f"    final_state: {final_state}")
    
    if jigsaw_count == 0:
        results.append("  No jigsaw blocks found!")
    else:
        results.insert(0 if not results else 1, f"  Jigsaw blocks: {jigsaw_count}")
    
    return "\n".join(results)

def main():
    # Also check the iron_golem.nbt at root level
    root_nbts = list(BASE.glob("*.nbt"))
    
    for folder_name in FOLDERS:
        folder = BASE / folder_name
        if not folder.exists():
            continue
        
        print(f"\n{'='*60}")
        print(f"  {folder_name.upper()}")
        print(f"{'='*60}")
        
        for nbt_file in sorted(folder.glob("**/*.nbt")):
            rel = nbt_file.relative_to(BASE)
            print(f"\n--- {rel} ---")
            print(inspect_nbt(nbt_file))
    
    # Root-level NBTs
    if root_nbts:
        print(f"\n{'='*60}")
        print(f"  ROOT LEVEL")
        print(f"{'='*60}")
        for nbt_file in sorted(root_nbts):
            print(f"\n--- {nbt_file.name} ---")
            print(inspect_nbt(nbt_file))

    # Summary
    print(f"\n{'='*60}")
    print("  SUMMARY: House pool connections per street")
    print(f"{'='*60}")
    
    streets_dir = BASE / "streets"
    if streets_dir.exists():
        for nbt_file in sorted(streets_dir.glob("*.nbt")):
            try:
                nbt = nbtlib.load(nbt_file, gzipped=True)
            except:
                nbt = nbtlib.load(nbt_file, gzipped=False)
            
            blocks = nbt.get("blocks", [])
            palette = nbt.get("palette", [])
            palette_names = {}
            for i, entry in enumerate(palette):
                palette_names[i] = str(entry.get("Name", ""))
            
            house_jigsaws = 0
            street_jigsaws = 0
            other_jigsaws = 0
            total_jigsaws = 0
            
            for block in blocks:
                state_idx = int(block.get("state", -1))
                block_name = palette_names.get(state_idx, "")
                if "jigsaw" in block_name.lower():
                    total_jigsaws += 1
                    nbt_data = block.get("nbt", {})
                    pool = str(nbt_data.get("pool", ""))
                    if "house" in pool.lower():
                        house_jigsaws += 1
                    elif "street" in pool.lower():
                        street_jigsaws += 1
                    else:
                        other_jigsaws += 1
            
            name = nbt_file.stem
            print(f"  {name:20s} | total: {total_jigsaws} | houses: {house_jigsaws} | streets: {street_jigsaws} | other: {other_jigsaws}")

if __name__ == "__main__":
    main()
