"""
For each house template:
- Flood-fill from boundary to find exterior air
- Convert exterior air to structure_void (terrain fills in)
- Keep interior air as minecraft:air (preserved by single_pool_element)
"""
import nbtlib
from collections import deque, Counter
import os

base = r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses'

for fname in sorted(os.listdir(base)):
    if not fname.endswith('.nbt'):
        continue
    path = os.path.join(base, fname)
    nbt = nbtlib.load(path)
    palette = nbt['palette']
    size = nbt['size']
    sx, sy, sz = int(size[0]), int(size[1]), int(size[2])

    # Find palette indices
    air_idx = None
    sv_idx = None
    for i, entry in enumerate(palette):
        name = str(entry['Name'])
        if name == 'minecraft:air':
            air_idx = i
        elif name == 'minecraft:structure_void':
            sv_idx = i

    if air_idx is None:
        print(f'{fname}: No air, skipping')
        continue

    # Add structure_void to palette if needed
    if sv_idx is None:
        sv_idx = len(palette)
        palette.append(nbtlib.Compound({'Name': nbtlib.String('minecraft:structure_void')}))

    # Find entrance jigsaw to know which face to skip during flood
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

    # Flood-fill from boundary, excluding entrance face
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

    # Convert exterior air to structure_void
    exterior_converted = 0
    interior_kept = 0
    for block in nbt['blocks']:
        x, y, z = int(block['pos'][0]), int(block['pos'][1]), int(block['pos'][2])
        if int(block['state']) == air_idx:
            pos = (x, y, z)
            if pos in visited:
                block['state'] = nbtlib.Int(sv_idx)
                exterior_converted += 1
            else:
                interior_kept += 1

    print(f'{fname}: exterior air->structure_void={exterior_converted}, interior air kept={interior_kept}')

    nbt.save(path)
