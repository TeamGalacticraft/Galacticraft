# This tool converts the capes.txt file to json.

import json
import os

capesTxt = open('../capes.txt', 'r')
print('Loaded capes.txt.')
players = []

for line in capesTxt:
    uuid = line.split(':')[0]
    cape = line.split(':')[1].split(' ')[0]
    name = line.split(':')[1].split(' ')[1]

    name = name[0:len(name)-1] # remove \n at end of name

    player = {}
    player['uuid'] = uuid
    player['cape'] = cape
    player['name'] = name

    players.append(player)
    print('Converted player {} to json format.'.format(name))

print('Players:', len(players))
capesTxt.close()

if os.path.exists("capes.json"):
    os.remove("capes.json")

capesJson = open('capes.json', 'x')

jsonObj = {}
jsonObj["players"] = players

jsonStr = json.dumps(jsonObj, indent=4)
capesJson.write(jsonStr)
capesJson.close()

print('Done.')