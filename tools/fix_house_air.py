"""
Convert interior air to cave_air and exterior air to structure_void in house NBTs.

Interior air = air blocks enclosed by solid walls/floors/ceilings (rooms).
Exterior air = air blocks connected to the template boundary (sky, terrain fill zones).

Uses 3D flood-fill from all boundary air positions to mark exterior air.
"""
import sys
import nbtlib
from nbtlib import String, Int
from collections import deque

def is_air_like(name):
    return name in ('minecraft:air', 'minecraft:cave_air')

def is_passthrough(name):
    """Blocks that don't form solid walls (air can flood through them)."""
    return is_air_like(name) or name == 'minecraft:structure_void'

def process_nbt(nbt_path, dry_run=False):
    nbt = nbtlib.load(nbt_path)
    palette = nbt['palette']
    blocks = nbt['blocks']
    size = nbt['size']
    sx, sy, sz = int(size[0]), int(size[1]), int(size[2])

    # Build palette name lookup
    palette_names = [str(entry['Name']) for entry in palette]

    # Find or create cave_air and structure_void palette entries
    cave_air_idx = None
    struct_void_idx = None
    for i, name in enumerate(palette_names):
        if name == 'minecraft:cave_air':
            cave_air_idx = i
        if name == 'minecraft:structure_void':
            struct_void_idx = i

    if cave_air_idx is None:
        cave_air_idx = len(palette)
        palette.append(nbtlib.Compound({'Name': String('minecraft:cave_air')}))
        palette_names.append('minecraft:cave_air')
        print(f"  Added cave_air to palette at index {cave_air_idx}")

    if struct_void_idx is None:
        struct_void_idx = len(palette)
        palette.append(nbtlib.Compound({'Name': String('minecraft:structure_void')}))
        palette_names.append('minecraft:structure_void')
        print(f"  Added structure_void to palette at index {struct_void_idx}")

    # Build 3D grid: grid[x][y][z] = block index in 'blocks' list, or -1
    # Also build state grid for fast lookups
    pos_to_idx = {}
    for bi, block in enumerate(blocks):
        pos = block['pos']
        x, y, z = int(pos[0]), int(pos[1]), int(pos[2])
        pos_to_idx[(x, y, z)] = bi

    def get_block_name(x, y, z):
        bi = pos_to_idx.get((x, y, z))
        if bi is None:
            return None  # position not in template (implicitly air/void)
        state_idx = int(blocks[bi]['state'])
        return palette_names[state_idx]

    # Find all air-like blocks
    air_positions = set()
    for (x, y, z), bi in pos_to_idx.items():
        name = palette_names[int(blocks[bi]['state'])]
        if is_air_like(name):
            air_positions.add((x, y, z))

    print(f"  Template size: {sx} x {sy} x {sz}")
    print(f"  Total blocks in template: {len(blocks)}")
    print(f"  Air-like blocks: {len(air_positions)}")

    # Flood fill from boundary to find exterior air
    # A position is "boundary" if any of its coordinates is at the edge of the template
    exterior = set()
    queue = deque()

    for (x, y, z) in air_positions:
        if x == 0 or x == sx - 1 or y == 0 or y == sy - 1 or z == 0 or z == sz - 1:
            exterior.add((x, y, z))
            queue.append((x, y, z))

    # BFS flood fill through air
    directions = [(1,0,0),(-1,0,0),(0,1,0),(0,-1,0),(0,0,1),(0,0,-1)]
    while queue:
        cx, cy, cz = queue.popleft()
        for dx, dy, dz in directions:
            nx, ny, nz = cx + dx, cy + dy, cz + dz
            if (nx, ny, nz) in air_positions and (nx, ny, nz) not in exterior:
                # Check bounds
                if 0 <= nx < sx and 0 <= ny < sy and 0 <= nz < sz:
                    exterior.add((nx, ny, nz))
                    queue.append((nx, ny, nz))

    interior = air_positions - exterior

    print(f"  Exterior air (-> structure_void): {len(exterior)}")
    print(f"  Interior air (-> cave_air): {len(interior)}")

    if dry_run:
        print("  DRY RUN - no changes saved")
        return

    # Apply changes
    for (x, y, z) in exterior:
        bi = pos_to_idx[(x, y, z)]
        blocks[bi]['state'] = Int(struct_void_idx)

    for (x, y, z) in interior:
        bi = pos_to_idx[(x, y, z)]
        blocks[bi]['state'] = Int(cave_air_idx)

    nbt.save(nbt_path)
    print(f"  Saved: {nbt_path}")


if __name__ == '__main__':
    import glob
    import os

    houses_dir = r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses'

    if len(sys.argv) > 1:
        # Process specific file
        for path in sys.argv[1:]:
            print(f"Processing: {path}")
            process_nbt(path)
    else:
        # Process just cheese_low_done for now
        path = os.path.join(houses_dir, 'cheese_low_done.nbt')
        print(f"Processing: {path}")
        process_nbt(path)
