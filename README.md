# Scrappy

## Introduction
Scrappy is a Web Scraping tool that uses Jira as its primary Jira Integrated Tool made to store snapshot of a website's data. 

## Setup
### Custom Jira Types
There are 4 custom Jira types.

![image](https://user-images.githubusercontent.com/63452934/229280333-b9986973-ef0a-423d-933f-99db39ca535b.png)

#### Scrappy Execution
The Jira used by the program to find urls to capture. Contains Folder and Url Issues. 

#### Scrappy Folder
Contains Url Issues. Used to group Url Issues together. 

#### Scrappy Url
Contains a url to test. 

#### Scrappy Snapshot
Contains data relating to a state of a url. 

### Issue Links
There are 2 types of custom Jira Links.

![tempsnip](https://user-images.githubusercontent.com/63452934/229280628-0e4455c7-5951-49e1-b8f4-49ca3ab995f2.png)

#### Contains
Used by Execution and Folders to reference Folder or Url Issues. This is parsed as apart of the issue tree. 

#### Snapshot
Used by Snapshot issues to reference the Url Issue. 

### Issue Workflows

#### Scrappy Tree Workflow
![image](https://user-images.githubusercontent.com/63452934/229280993-5939bf1c-b3b6-4dd3-9ecd-afda6031d4c2.png)
Used to determine the state of a issue. Only issues InUse will be snapshotted. 

#### Scrappy Snapshot Workflow
![image](https://user-images.githubusercontent.com/63452934/229281057-7eae5cc2-1785-415f-9a50-d9440282ca04.png)
Used by user to view newly create snapshots.

