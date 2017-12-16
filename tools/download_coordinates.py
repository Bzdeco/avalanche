#!/usr/local/bin/python3

import urllib.request
import re
from bs4 import BeautifulSoup

regex = re.compile(r'M-34-(100-B|101-A)-[abcd](-[1234]){2}')
some_spans = []

def _extract_from_row(line):
    filename = regex.search(line).group(0)
    link = line.split("\"")[1]
    return link, filename

def _extract_coords(url):
    soup = BeautifulSoup(urllib.request.urlopen(url).read().decode("utf-8"), 'html.parser')
    coords_spans = [span for span in soup.find_all('span') if span.get('class') == ['longitude'] or span.get('class') == ['latitude']]
    latitudes = [float(span.string) for span in coords_spans if span['class'] == ['latitude']]
    longitudes = [float(span.string) for span in coords_spans if span['class'] == ['longitude']]
    return sum(latitudes)/2, sum(longitudes)/2

with open("data_extracted.html", "r") as f, open("map2.txt", "w") as dest:
    for line in f:
        link, filename = _extract_from_row(line)
        latitude, longitude = _extract_coords(link)
        result_line = "put(\""+filename+"\", new Coords("+str(latitude)+"f, "+str(longitude)+"f));\n"
        dest.write(result_line)
        print (result_line)


