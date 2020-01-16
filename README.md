## nexus-npm-repo-cleaner

##### Description:

Nexus task for cleaning old npm packages

##### Usage:

###### Before adding script to your task please define next vars:
  Objects to save for each component - ```def retentionCount = 10;```
  Your repository name - ```def repositoryName = 'my_nexus_repo';```
  def blacklist = ```["component_group_1/component", "component_group_2/component"].toArray();```
