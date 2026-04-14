import nbtlib

base = r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses'
files = {
    'oblique_big_house_done.nbt': 5,
    'small_round_house_01_done.nbt': 2,
    'small_round_house_02_done.nbt': 2,
}

for fname, entrance_y in files.items():
    path = f'{base}\\{fname}'
    nbt = nbtlib.load(path)
    palette = nbt['palette']

    # Check if cave_air already exists in palette
    cave_air_idx = None
    for i, entry in enumerate(palette):
        if str(entry['Name']) == 'minecraft:cave_air':
            cave_air_idx = i
            break

    # Add cave_air to palette if needed
    if cave_air_idx is None:
        cave_air_idx = len(palette)
        palette.append(nbtlib.Compound({
            'Name': nbtlib.String('minecraft:cave_air')
        }))
        print(f'{fname}: Added cave_air at palette index {cave_air_idx}')

    # Find the air palette index
    air_idx = None
    for i, entry in enumerate(palette):
        if str(entry['Name']) == 'minecraft:air':
            air_idx = i
            break

    if air_idx is None:
        print(f'{fname}: No air found in palette, skipping')
        continue

    # Convert air -> cave_air for blocks at Y <= entrance_y
    count = 0
    for block in nbt['blocks']:
        if int(block['state']) == air_idx and int(block['pos'][1]) <= entrance_y:
            block['state'] = nbtlib.Int(cave_air_idx)
            count += 1

    nbt.save(path)
    print(f'{fname}: Converted {count} air blocks (Y<=  {entrance_y}) to cave_air')
