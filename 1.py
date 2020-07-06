#!/usr/bin/env python
import requests
import os
import re

user = 'zuser'
passwd = 'vVk858dC'
registry_url = 'https://nexus.zdevcode.com/repository/docker-internal/v2'
registry_name = 'docker-internal'
nexus_url = 'https://nexus.zdevcode.com/repository'
headers = {'Accept': 'application/vnd.docker.distribution.manifest.v2+json'}

MANIFEST_NAME = (os.getenv('MANIFEST_NAME', "merkeleon/absent"))

def sorted_nicely(l):
    convert = lambda text: int(text) if text.isdigit() else text
    alphanum_key = lambda key: [convert(c) for c in re.split('([0-9]+)', key)]
    return sorted(l, key=alphanum_key)

if __name__ == '__main__':
    # main()
    url_tags = "%s/%s/tags/list" % (registry_url, MANIFEST_NAME)

    r = requests.get(url_tags, auth=(user, passwd)).json()
    rj = r['tags']

    if "latest" in rj:
        rj.remove("latest")  # Exclude a tag with name latest

    rj_sorted = []
    for x in sorted_nicely(rj):
        rj_sorted.append(str(x))

    print("%s" % (rj_sorted[-1]))

