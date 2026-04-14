import nbtlib
from collections import Counter

base = r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses'
files = [
    'oblique_big_house_done.nbt',
    'small_round_house_01_done.nbt',
    'small_round_house_02_done.nbt',
]

for fname in files:
    path = f'{base}\\{fname}'
    nbt = nbtlib.load(path)
    palette = nbt['palette']
    size = nbt['size']
    sx, sy, sz = int(size[0]), int(size[1]), int(size[2])
    total_positions = sx * sy * sz
    print(f'\n=== {fname} (size {sx}x{sy}x{sz} = {total_positions} positions) ===')

    # Show palette entries
    for i, entry in enumerate(palette):
        print(f'  [{i}] {entry["Name"]}')

    # Count blocks by type and Y
    type_y = {}
    for block in nbt['blocks']:
        name = str(palette[int(block['state'])]['Name'])
        y = int(block['pos'][1])
        key = name
        if key not in type_y:
            type_y[key] = Counter()
        type_y[key][y] += 1

    # Show structure_void, air, cave_air distribution
    for block_type in ['minecraft:structure_void', 'minecraft:air', 'minecraft:cave_air']:
        if block_type in type_y:
            print(f'  {block_type}: {dict(sorted(type_y[block_type].items()))}')
        else:
            print(f'  {block_type}: (not present)')

    # Count total explicit blocks vs total positions
    print(f'  Total blocks in NBT: {len(nbt["blocks"])}')
    print(f'  Total positions: {total_positions}')
