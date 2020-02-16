# This tool takes a standard Blockbench Modded Minecraft (Java) export and converts it for use with Fabric 1.14 (and possibly 1.15).
# Tested on Windows, Python 3.8


# DISCLAIMER: This is a very basic converter, and may not work all the time.


import os


print("DISCLAIMER: The code generated may require additional conversion steps not listed at the end.\nFor example, with MoonVillager I had to add a line of code that disabled the hat.")
print("Use at own risk!")
print("----------------")

input("The file you want to convert should be in the same directory as this program. Type anything and hit ENTER to proceed. ")
oldFile = open((os.getcwd() + "\\" + str(input("Enter the file name: "))), "r")
oldFileData = oldFile.read()
oldFile.close()

entityName = str(input("What is the name of the entity? Example format is \"MoonVillager\" without quotes: "))
extendsName = str(input("What is the model this entity inherits from? Example is \"VillagerResemblingModel\" without quotes: "))

print("Converting...")

newFileData = """/*
 * Copyright (c) 2020 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.entity.{0};

import net.minecraft.client.model.Box;
import net.minecraft.client.model.Model;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */

public class {1}Model<{2}_ENTITY> extends {3}<{1}Entity> {{

    private final Model baseModel;

    public {1}Model(int scale, int textureWidth, int textureHeight) {{
	super(scale, textureWidth, textureHeight);
	this.baseModel = new Model();
""".format(entityName.lower(), entityName, entityName.upper(), extendsName)

oldFileSplit = oldFileData.split('\n')
processedLines = ""
skip = ''
for line in oldFileSplit[22:]:
    if skip == '':
        if "void setRotationAngle" in line:
            skip = '}'
            continue
        if "setRotationAngle" in line:
            continue
        elif "ModelRenderer" in line:
            continue
        elif "@Override" in line:
            skip = '}'
            continue
        else:
            line = line.replace("ModelBox", "Box")
            line = line.replace("cubeList", "boxes")
            
            processedLines += line[1:] + '\n'
            
    elif line.strip().replace('\t', '') == skip:
        skip = ''
        continue

newFileData += processedLines

newFile = open(os.getcwd() + '\\' + entityName + "Model_convert.java", "w")
newFile.write(newFileData)
newFile.close()

print("Finished conversion. Open {0}Model_convert.java for converted file.".format(entityName))
print("You are not done yet though!")
print("Put a \"this\" before every variable name.")
print("Check to make sure that the variables exist. If you are making a biped, leg0 should be leftLeg and leg1 should be rightLeg.")
print("Add broken imports.")
print("Add *body part*.boxes.clear() for every body part.")
print("Remove the _convert in this filename. Move it to entity/{0}/{0}Model.java".format(entityName))
print("Change the renderer to this model as well.")
print("------------------------------------------")
print("Alright, that should be about it. Good luck!")
input("Type anything and hit ENTER when done.")
