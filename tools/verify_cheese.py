import nbtlib
from collections import Counter
nbt = nbtlib.load(r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses\cheese_low_done.nbt')
palette = nbt['palette']
for block in nbt['blocks']:
    name = str(palette[int(block['state'])]['Name'])
    if 'jigsaw' in name:
        p = block['pos']
        d = block.get('nbt', {})
        pool = str(d.get('pool', '?'))
        target = str(d.get('target', '?'))
        fs = str(d.get('final_state', '?'))
        print(f'  ({int(p[0])},{int(p[1])},{int(p[2])}) pool={pool} target={target} final_state={fs}')

air_y = Counter()
cave_y = Counter()
for block in nbt['blocks']:
    name = str(palette[int(block['state'])]['Name'])
    y = int(block['pos'][1])
    if name == 'minecraft:air': air_y[y] += 1
    elif name == 'minecraft:cave_air': cave_y[y] += 1
print()
print('Air by Y:', dict(sorted(air_y.items())))
print('Cave_air by Y:', dict(sorted(cave_y.items())))
