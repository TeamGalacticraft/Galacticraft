"""Fix villager jigsaw final_state in all house NBTs: change to galacticraft:lunar_home_anchor"""
import nbtlib
import os

base = r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses'

for fname in sorted(os.listdir(base)):
    if not fname.endswith('.nbt'):
        continue
    path = os.path.join(base, fname)
    nbt = nbtlib.load(path)
    palette = nbt['palette']
    changed = 0
    for block in nbt['blocks']:
        name = str(palette[int(block['state'])]['Name'])
        if 'jigsaw' in name:
            d = block.get('nbt', {})
            pool = str(d.get('pool', ''))
            fs = str(d.get('final_state', ''))
            if 'villagers' in pool and fs != 'galacticraft:lunar_home_anchor':
                old_fs = fs
                d['final_state'] = nbtlib.String('galacticraft:lunar_home_anchor')
                changed += 1
                p = block['pos']
                print(f'  {fname} ({int(p[0])},{int(p[1])},{int(p[2])}): {old_fs} -> galacticraft:lunar_home_anchor')
    if changed:
        nbt.save(path)
    else:
        # Check if any villager jigsaws exist
        has_villager = any('villagers' in str(block.get('nbt', {}).get('pool', ''))
                          for block in nbt['blocks']
                          if 'jigsaw' in str(palette[int(block['state'])]['Name']))
        if has_villager:
            print(f'{fname}: already correct')
        else:
            print(f'{fname}: no villager jigsaws')
