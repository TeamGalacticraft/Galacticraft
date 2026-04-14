"""
Fix villager-spawning jigsaw blocks in house NBTs to use lunar_home_anchor as final_state
instead of tin_decoration, so villagers get a HOME point to return to at night.
"""
import nbtlib
import pathlib

house_dir = pathlib.Path('src/main/resources/data/galacticraft/structure/village/moon/highlands/houses')
total_fixed = 0

for nbt_file in sorted(house_dir.glob('*.nbt')):
    nbt = nbtlib.load(nbt_file)
    blocks = nbt.get('blocks', [])
    palette = nbt.get('palette', [])
    fixed = 0

    for block in blocks:
        idx = int(block.get('state', 0))
        if idx < len(palette):
            bname = str(palette[idx].get('Name', ''))
            if bname == 'minecraft:jigsaw':
                d = block.get('nbt', {})
                pool = str(d.get('pool', ''))
                if 'villagers' in pool:
                    old_fs = str(d.get('final_state', ''))
                    if old_fs != 'galacticraft:lunar_home_anchor':
                        d['final_state'] = nbtlib.String('galacticraft:lunar_home_anchor')
                        pos = block.get('pos', [])
                        print("  {}: ({},{},{}) {} -> galacticraft:lunar_home_anchor".format(
                            nbt_file.stem, int(pos[0]), int(pos[1]), int(pos[2]), old_fs))
                        fixed += 1

    if fixed > 0:
        nbt.save(nbt_file)
        total_fixed += fixed

print("\nTotal fixed: {} villager jigsaws across all houses".format(total_fixed))
