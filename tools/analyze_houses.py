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
    print(f'\n=== {fname} ===')
    print(f'Size: {int(size[0])}x{int(size[1])}x{int(size[2])}')

    # Find jigsaws
    entrance_y = None
    for block in nbt['blocks']:
        name = str(palette[int(block['state'])]['Name'])
        if 'jigsaw' in name:
            p = block['pos']
            d = block.get('nbt', {})
            pool = str(d.get('pool', '?'))
            target = str(d.get('target', '?'))
            fs = str(d.get('final_state', '?'))
            y = int(p[1])
            print(f'  Jigsaw ({int(p[0])},{y},{int(p[2])}) pool={pool} target={target} final_state={fs}')
            if 'building_entrance' in target:
                entrance_y = y

    # Air distribution
    air_y = Counter()
    cave_y = Counter()
    for block in nbt['blocks']:
        name = str(palette[int(block['state'])]['Name'])
        y = int(block['pos'][1])
        if name == 'minecraft:air':
            air_y[y] += 1
        elif name == 'minecraft:cave_air':
            cave_y[y] += 1

    print(f'  Entrance jigsaw Y: {entrance_y}')
    print(f'  Air by Y:      {dict(sorted(air_y.items()))}')
    print(f'  Cave_air by Y:  {dict(sorted(cave_y.items()))}')

    # Count air below entrance
    if entrance_y is not None:
        below = sum(c for y, c in air_y.items() if y <= entrance_y)
        print(f'  Air blocks at Y <= {entrance_y} (to convert): {below}')
