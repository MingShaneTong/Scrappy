# Scrappy

## Introduction
Scrappy is a Web Scraping tool that uses Jira as its primary Jira Integrated Tool made to store snapshot of a website's data. 

## Prerequisites
 - Java
 - Maven
 - Atlassian Jira

## Build Instructions
1. Create an API token by visiting [/manage-profile/security/api-tokens](https://id.atlassian.com/manage-profile/security/api-tokens) on your server.
2. Create issues types and links to the specifications on [Jira Issues](/docs/jira-issue.md)
3. Create a file '.settings' and in the file put the following format.
```
${Jira Server Url} Eg. https://USER.atlassian.net/
${Project}
${Authentication Login, email or username}
${API Token}
${Execution Jira Issue Key}
```
5. To run the program, use the comment `mvn compile exec:java`