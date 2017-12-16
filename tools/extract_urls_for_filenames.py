#! /usr/local/bin/python3

import re

regex = re.compile(r'M-34-(100-B|101-A)-[abcd](-[1234]){2}')

found = []
with open("tools/data.html") as file:
    for line in file:
        match = regex.search(line)
        if match:
            found.append(line)

with open("tools/data_extracted.html", "w") as file2:
    for line in found:
        file2.write(line)

print (found)