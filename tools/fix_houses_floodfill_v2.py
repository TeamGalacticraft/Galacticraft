"""
Smarter flood-fill: don't seed from the face containing the entrance jigsaw,
so the flood can't enter the building through the door.
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

    # Step 1: Revert any existing cave_air -> air
    reverted = 0
    if cave_air_idx is not None:
        for block in nbt['blocks']:
            if int(block['state']) == cave_air_idx:
                block['state'] = nbtlib.Int(air_idx)
                reverted += 1

    # Ensure cave_air palette entry
    if cave_air_idx is None:
        cave_air_idx = len(palette)
        palette.append(nbtlib.Compound({'Name': nbtlib.String('minecraft:cave_air')}))

    # Find entrance jigsaw to determine which face to skip
    entrance_pos = None
    for block in nbt['blocks']:
        name = str(palette[int(block['state'])]['Name'])
        if 'jigsaw' in name:
            d = block.get('nbt', {})
            if 'building_entrance' in str(d.get('target', '')):
                entrance_pos = (int(block['pos'][0]), int(block['pos'][1]), int(block['pos'][2]))
                break

    # Determine which boundary face the entrance is on
    skip_face = None
    if entrance_pos:
        ex, ey, ez = entrance_pos
        if ex == 0: skip_face = ('x', 0)
        elif ex == sx - 1: skip_face = ('x', sx - 1)
        elif ez == 0: skip_face = ('z', 0)
        elif ez == sz - 1: skip_face = ('z', sz - 1)
        elif ey == 0: skip_face = ('y', 0)
        elif ey == sy - 1: skip_face = ('y', sy - 1)

    print(f'\n=== {fname} ===')
    print(f'  Size: {sx}x{sy}x{sz}, entrance Y={entrance_y}')
    print(f'  Entrance at: {entrance_pos}, skip face: {skip_face}')
    print(f'  Reverted {reverted} cave_air -> air')

    # Build grid
    grid = {}
    block_map = {}
    for block in nbt['blocks']:
        x, y, z = int(block['pos'][0]), int(block['pos'][1]), int(block['pos'][2])
        state = int(block['state'])
        name = str(palette[state]['Name'])
        grid[(x, y, z)] = name
        block_map[(x, y, z)] = block

    def is_air_or_void(pos):
        if pos not in grid:
            return True
        return grid[pos] == 'minecraft:air'

    def is_on_skip_face(x, y, z):
        if skip_face is None:
            return False
        axis, val = skip_face
        if axis == 'x': return x == val
        if axis == 'y': return y == val
        if axis == 'z': return z == val
        return False

    # Flood-fill from boundary, EXCLUDING the entrance face
    visited = set()
    queue = deque()

    for x in range(sx):
        for y in range(sy):
            for z in range(sz):
                is_boundary = (x == 0 or x == sx - 1 or y == 0 or y == sy - 1 or z == 0 or z == sz - 1)
                if is_boundary and not is_on_skip_face(x, y, z):
                    pos = (x, y, z)
                    if is_air_or_void(pos) and pos not in visited:
                        visited.add(pos)
                        queue.append(pos)

    while queue:
        cx, cy, cz = queue.popleft()
        for dx, dy, dz in [(1,0,0),(-1,0,0),(0,1,0),(0,-1,0),(0,0,1),(0,0,-1)]:
            nx, ny, nz = cx + dx, cy + dy, cz + dz
            if 0 <= nx < sx and 0 <= ny < sy and 0 <= nz < sz:
                npos = (nx, ny, nz)
                if npos not in visited and is_air_or_void(npos):
                    visited.add(npos)
                    queue.append(npos)

    # Convert interior underground air to cave_air
    converted = 0
    exterior_kept = 0
    for x in range(sx):
        for y in range(sy):
            for z in range(sz):
                if y > entrance_y:
                    continue
                pos = (x, y, z)
                if pos in grid and grid[pos] == 'minecraft:air':
                    if pos not in visited:
                        block_map[pos]['state'] = nbtlib.Int(cave_air_idx)
                        converted += 1
                    else:
                        exterior_kept += 1

    print(f'  Interior air -> cave_air: {converted}')
    print(f'  Exterior air kept as air: {exterior_kept}')

    # Verify
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
