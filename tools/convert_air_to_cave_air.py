"""Convert minecraft:air to minecraft:cave_air in cheese_low_done.nbt for testing."""
import nbtlib
from nbtlib import String

nbt_path = r'src\main\resources\data\galacticraft\structure\village\moon\highlands\houses\cheese_low_done.nbt'
nbt = nbtlib.load(nbt_path)

palette = nbt['palette']
old_name = str(palette[19]['Name'])
print(f'Before: palette[19] = {old_name}')

palette[19]['Name'] = String('minecraft:cave_air')
new_name = str(palette[19]['Name'])
print(f'After: palette[19] = {new_name}')

nbt.save(nbt_path)
print('Saved successfully.')
