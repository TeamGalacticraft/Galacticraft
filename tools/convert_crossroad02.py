"""
Convert 2 street jigsaws in crossroad_02 to house jigsaws.
This turns a pure-road crossroad into one that places houses on 2 arms.
"""
import nbtlib
import pathlib

nbt_file = pathlib.Path('src/main/resources/data/galacticraft/structure/village/moon/highlands/streets/crossroad_02.nbt')
nbt = nbtlib.load(nbt_file)
blocks = nbt.get('blocks', [])
palette = nbt.get('palette', [])

# Convert the south (8,1,15) and east (15,1,8) street jigsaws to house jigsaws
converted = 0
for block in blocks:
    idx = int(block.get('state', 0))
    if idx < len(palette):
        bname = str(palette[idx].get('Name', ''))
        if bname == 'minecraft:jigsaw':
            d = block.get('nbt', {})
            pool = str(d.get('pool', ''))
            pos = block.get('pos', [])
            x, y, z = int(pos[0]), int(pos[1]), int(pos[2])
            
            # Convert south arm (8,1,15) and east arm (15,1,8) to house jigsaws
            if pool == 'galacticraft:village/moon/highlands/streets' and (
                (x == 8 and y == 1 and z == 15) or  # south
                (x == 15 and y == 1 and z == 8)      # east
            ):
                d['pool'] = nbtlib.String('galacticraft:village/moon/highlands/houses')
                d['name'] = nbtlib.String('minecraft:building_entrance')
                d['target'] = nbtlib.String('minecraft:building_entrance')
                d['final_state'] = nbtlib.String('galacticraft:moon_turf')
                # Move from y=1 to y=0 (house jigsaws are at ground level)
                pos[1] = nbtlib.Int(0)
                converted += 1
                print(f'  Converted jigsaw at ({x},{y},{z}) -> ({x},0,{z}): streets -> houses')

nbt.save(nbt_file)
print(f'\nTotal converted: {converted} jigsaws')
print(f'crossroad_02 now has: 2 street jigsaws + 2 house jigsaws + 5 decor jigsaws')
