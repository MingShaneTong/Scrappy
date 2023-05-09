# Instructions

## Grammar
```text
PRGM ::= [STMT]+
STMT ::= CAPTURE | CLICK | COMMENT | SCREENSHOT | VISIT | WAITFOR ";"
SELECTOR ::= BRACKETEDSTRING "with selector" BRACKETEDSTRING
BRACKETEDSTRING ::= "(" [TEXT]+ ")"
TEXT ::= .*
CAPTURE ::= "Capture" CAPTURETYPE "from" SELECTOR "to file" BRACKETEDSTRING
CAPTURETYPE ::= "HTML" | "TextContent"
CLICK ::= "Click" SELECTOR
COMMENT ::= "//" [TEXT]*
SCREENSHOT ::= "Screenshot" SELECTOR "as file" BRACKETEDSTRING
VISIT ::= "Visit" BRACKETEDSTRING
WAITFOR ::= "WaitFor" SELECTOR ;
```

## Parameters
| Name         | Syntax                                      | Input                              | Description                               |
|--------------|---------------------------------------------|------------------------------------|-------------------------------------------|
| SELECTOR     | ` {description} " with selector " {xpath} ` | description: String, xpath: String | Represents a element selector on the page |
| CAPTURE_TYPE | `['HTML', 'TextContent']`                   |                                    | Either HTML Or TextContent                |

## Instructions
| Description              | Syntax                                                        | Input           |
|--------------------------|---------------------------------------------------------------|-----------------|
| Captures content to file | `Capture /CAPTURE_TYPE/ from /SELECTOR/ to file " {file} " ;` | file: String    |
| Click element on page    | `Click /SELECTOR/ ;`                                          |                 |
| Comment, No Execution    | `// {comment} ;`                                              | comment: String |
| Screenshot element       | `Screenshot /SELECTOR/ as file " {file} " ;`                  | file: String    |
| Visit relative page      | `Visit ( {page} ) ;`                                          | page: String    |
| Wait For Selector        | `WaitFor /SELECTOR/ ; `                                       |                 |

## Examples
```text
// Hello world ;
Visit ( / ) ;
Click ( Close Button ) with selector ( a.close ) ;
WaitFor ( body ) with selector ( body ) ;
Screenshot ( Listing Table ) with selector ( div.listings table ) as file ( listings.png ) ;
Capture HTML from ( Listing Table ) with selector ( div.listings table ) to file ( listings.html ) ; 
Capture TextContent from ( Listing Table ) with selector ( div.listings table ) to file ( listings.txt ) ;
```