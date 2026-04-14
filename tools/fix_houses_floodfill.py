"""
For each house template:
1. Revert any cave_air back to air
2. Build a 3D grid
3. Flood-fill from boundary faces to find exterior air
4. Convert only INTERIOR (unreached) underground air to cave_air
   Leave EXTERIOR underground air as minecraft:air (terrain overwrites it)
"""
import nbtlib
from collections import deque, Counter

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
    size = nbt['size']
    sx, sy, sz = int(size[0]), int(size[1]), int(size[2])

    # Find palette indices
    air_idx = None
    cave_air_idx = None
    for i, entry in enumerate(palette):
        name = str(entry['Name'])
        if name == 'minecraft:air':
            air_idx = i
        elif name == 'minecraft:cave_air':
            cave_air_idx = i

    # Step 1: Revert cave_air -> air at all Y levels
    reverted = 0
    if cave_air_idx is not None:
        for block in nbt['blocks']:
            if int(block['state']) == cave_air_idx:
                block['state'] = nbtlib.Int(air_idx)
                reverted += 1

    print(f'\n=== {fname} ===')
    print(f'  Size: {sx}x{sy}x{sz}, entrance Y={entrance_y}')
    print(f'  Reverted {reverted} cave_air -> air')

    # Ensure cave_air palette entry exists
    if cave_air_idx is None:
        cave_air_idx = len(palette)
        palette.append(nbtlib.Compound({'Name': nbtlib.String('minecraft:cave_air')}))

    # Step 2: Build 3D grid (None = not in blocks list, i.e. implicit void)
    grid = {}
    block_map = {}  # (x, y, z) -> block reference
    for block in nbt['blocks']:
        x, y, z = int(block['pos'][0]), int(block['pos'][1]), int(block['pos'][2])
        state = int(block['state'])
        name = str(palette[state]['Name'])
        grid[(x, y, z)] = name
        block_map[(x, y, z)] = block

    # Step 3: Flood-fill from ALL boundary positions to find exterior air
    # A block is "passable" if it's air (or not present = implicit void)
    # We flood from edges of the bounding box
    def is_air_or_void(pos):
        if pos not in grid:
            return True  # Implicit void = terrain fills = passable from outside
        return grid[pos] == 'minecraft:air'

    visited = set()
    queue = deque()

    # Seed: all boundary positions that are air or void
    for x in range(sx):
        for y in range(sy):
            for z in range(sz):
                if x == 0 or x == sx - 1 or y == 0 or y == sy - 1 or z == 0 or z == sz - 1:
                    pos = (x, y, z)
                    if is_air_or_void(pos) and pos not in visited:
                        visited.add(pos)
                        queue.append(pos)

    # BFS flood-fill
    while queue:
        cx, cy, cz = queue.popleft()
        for dx, dy, dz in [(1,0,0),(-1,0,0),(0,1,0),(0,-1,0),(0,0,1),(0,0,-1)]:
            nx, ny, nz = cx + dx, cy + dy, cz + dz
            if 0 <= nx < sx and 0 <= ny < sy and 0 <= nz < sz:
                npos = (nx, ny, nz)
                if npos not in visited and is_air_or_void(npos):
                    visited.add(npos)
                    queue.append(npos)

    # Step 4: Convert interior underground air to cave_air
    converted = 0
    exterior_underground = 0
    for x in range(sx):
        for y in range(sy):
            for z in range(sz):
                if y > entrance_y:
                    continue  # Above ground: leave as-is
                pos = (x, y, z)
                if pos in grid and grid[pos] == 'minecraft:air':
                    if pos not in visited:
                        # Interior air at underground level -> cave_air
                        block_map[pos]['state'] = nbtlib.Int(cave_air_idx)
                        converted += 1
                    else:
                        exterior_underground += 1

    print(f'  Interior air -> cave_air: {converted}')
    print(f'  Exterior air left as air: {exterior_underground}')

    # Verify distribution
    air_y = Counter()
    cave_y = Counter()
    for block in nbt['blocks']:
        name = str(palette[int(block['state'])]['Name'])
        y = int(block['pos'][1])
        if name == 'minecraft:air':
            air_y[y] += 1
        elif name == 'minecraft:cave_air':
            cave_y[y] += 1

    print(f'  Air by Y:      {dict(sorted(air_y.items()))}')
    print(f'  Cave_air by Y:  {dict(sorted(cave_y.items()))}')

    nbt.save(path)
    print(f'  Saved.')
