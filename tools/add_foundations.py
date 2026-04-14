"""
Add moon_rock foundation blocks under each house template.
For each column (x,z):
  - Find the lowest solid block (not air/structure_void/cave_air)
  - Fill all structure_void below it with moon_rock
  - Skip columns that are entirely exterior (no solid blocks)
"""
import nbtlib
import os

base = r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses'
SKIP_BLOCKS = {'minecraft:air', 'minecraft:cave_air', 'minecraft:structure_void'}

for fname in sorted(os.listdir(base)):
    if not fname.endswith('.nbt'):
        continue
    path = os.path.join(base, fname)
    nbt = nbtlib.load(path)
    palette = nbt['palette']
    size = nbt['size']
    sx, sy, sz = int(size[0]), int(size[1]), int(size[2])

    # Find or add moon_rock palette index
    moon_rock_idx = None
    sv_idx = None
    for i, entry in enumerate(palette):
        name = str(entry['Name'])
        if name == 'galacticraft:moon_rock':
            moon_rock_idx = i
        elif name == 'minecraft:structure_void':
            sv_idx = i

    if moon_rock_idx is None:
        moon_rock_idx = len(palette)
        palette.append(nbtlib.Compound({'Name': nbtlib.String('galacticraft:moon_rock')}))

    if sv_idx is None:
        print(f'{fname}: no structure_void, skipping')
        continue

    # Build position -> block map
    block_map = {}
    for block in nbt['blocks']:
        x, y, z = int(block['pos'][0]), int(block['pos'][1]), int(block['pos'][2])
        block_map[(x, y, z)] = block

    filled = 0
    for x in range(sx):
        for z in range(sz):
            # Find lowest solid block in this column
            lowest_solid_y = None
            for y in range(sy):
                pos = (x, y, z)
                if pos in block_map:
                    state = int(block_map[pos]['state'])
                    name = str(palette[state]['Name'])
                    if name not in SKIP_BLOCKS:
                        lowest_solid_y = y
                        break

            if lowest_solid_y is None:
                continue  # Entirely exterior column, skip

            # Fill structure_void below the lowest solid with moon_rock
            for y in range(lowest_solid_y):
                pos = (x, y, z)
                if pos in block_map:
                    state = int(block_map[pos]['state'])
                    if state == sv_idx:
                        block_map[pos]['state'] = nbtlib.Int(moon_rock_idx)
                        filled += 1

    if filled > 0:
        nbt.save(path)
    print(f'{fname}: filled {filled} foundation blocks')
