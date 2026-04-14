"""Revert cave_air back to air in all 4 house templates."""
import nbtlib
from collections import Counter

base = r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses'
files = [
    'cheese_low_done.nbt',
    'oblique_big_house_done.nbt',
    'small_round_house_01_done.nbt',
    'small_round_house_02_done.nbt',
]

for fname in files:
    path = f'{base}\\{fname}'
    nbt = nbtlib.load(path)
    palette = nbt['palette']

    air_idx = None
    cave_air_idx = None
    for i, entry in enumerate(palette):
        name = str(entry['Name'])
        if name == 'minecraft:air':
            air_idx = i
        elif name == 'minecraft:cave_air':
            cave_air_idx = i

    if cave_air_idx is None:
        print(f'{fname}: No cave_air found, skipping')
        continue

    count = 0
    for block in nbt['blocks']:
        if int(block['state']) == cave_air_idx:
            block['state'] = nbtlib.Int(air_idx)
            count += 1

    # Remove cave_air from palette by not saving it
    # Actually just leave the palette entry - unused entries are harmless
    # and removing would require re-indexing all blocks

    nbt.save(path)
    print(f'{fname}: Reverted {count} cave_air -> air')

    # Verify
    air_y = Counter()
    cave_y = Counter()
    for block in nbt['blocks']:
        name = str(palette[int(block['state'])]['Name'])
        y = int(block['pos'][1])
        if name == 'minecraft:air': air_y[y] += 1
        elif name == 'minecraft:cave_air': cave_y[y] += 1
    print(f'  Air by Y: {dict(sorted(air_y.items()))}')
    if cave_y:
        print(f'  Cave_air by Y: {dict(sorted(cave_y.items()))}')
