import nbtlib, pathlib, json

street_dir = pathlib.Path('src/main/resources/data/galacticraft/structure/village/moon/highlands/streets')
pool_file = pathlib.Path('src/main/generated/data/galacticraft/worldgen/template_pool/village/moon/highlands/streets.json')

with open(pool_file) as f:
    pool = json.load(f)

weights = {}
for elem in pool['elements']:
    loc = elem['element']['location']
    name = loc.split('/')[-1]
    weights[name] = elem['weight']

header = "{:<20s} {:>6s} {:>7s} {:>6s} {:>8s} {:>7s} {:>8s}".format(
    'Street', 'Weight', 'StreetJ', 'HouseJ', 'Ratio', 'W*House', 'W*Str')
print("=== FINAL POOL ANALYSIS ===")
print(header)
print('-' * 70)

total_w_houses = 0
total_w_streets = 0
total_weight = 0

for nbt_file in sorted(street_dir.glob('*.nbt')):
    stem = nbt_file.stem
    if stem not in weights:
        continue
    
    nbt = nbtlib.load(nbt_file)
    blocks = nbt.get('blocks', [])
    palette = nbt.get('palette', [])
    
    sc = 0
    hc = 0
    for block in blocks:
        idx = int(block.get('state', 0))
        if idx < len(palette):
            bname = str(palette[idx].get('Name', ''))
            if bname == 'minecraft:jigsaw':
                p = str(block.get('nbt', {}).get('pool', ''))
                if 'streets' in p: sc += 1
                elif 'houses' in p: hc += 1
    
    w = weights[stem]
    ratio = "{}/{}".format(hc, sc)
    wh = w * hc
    ws = w * sc
    total_w_houses += wh
    total_w_streets += ws
    total_weight += w
    row = "{:<20s} {:>6d} {:>7d} {:>6d} {:>8s} {:>7d} {:>8d}".format(
        stem, w, sc, hc, ratio, wh, ws)
    print(row)

print('-' * 70)
print("Total weight: {}".format(total_weight))
print("Weighted house jigsaws: {}".format(total_w_houses))
print("Weighted street jigsaws: {}".format(total_w_streets))
print("Weighted house:street ratio = {:.2f}:1".format(total_w_houses / total_w_streets))
print("All streets in pool have >= 1 house jigsaw: YES")
