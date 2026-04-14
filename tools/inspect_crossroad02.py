import nbtlib
import pathlib

nbt_file = pathlib.Path('src/main/resources/data/galacticraft/structure/village/moon/highlands/streets/crossroad_02.nbt')
nbt = nbtlib.load(nbt_file)
blocks = nbt.get('blocks', [])
palette = nbt.get('palette', [])

for i, block in enumerate(blocks):
    idx = int(block.get('state', 0))
    if idx < len(palette):
        pentry = palette[idx]
        bname = str(pentry.get('Name', ''))
        if bname == 'minecraft:jigsaw':
            d = block.get('nbt', {})
            pos = block.get('pos', [])
            props = pentry.get('Properties', {})
            print(f'Block index {i}:')
            print(f'  pos: ({int(pos[0])},{int(pos[1])},{int(pos[2])})')
            print(f'  palette idx: {idx}')
            print(f'  palette props: {dict(props)}')
            print(f'  pool: {d.get("pool", "")}')
            print(f'  name: {d.get("name", "")}')
            print(f'  target: {d.get("target", "")}')
            print(f'  final_state: {d.get("final_state", "")}')
            print(f'  joint: {d.get("joint", "")}')
            print()

# Also show all unique palette entries for jigsaw blocks
print("=== Jigsaw palette entries ===")
for i, p in enumerate(palette):
    if str(p.get('Name', '')) == 'minecraft:jigsaw':
        print(f'  Palette[{i}]: {dict(p.get("Properties", {}))}')
