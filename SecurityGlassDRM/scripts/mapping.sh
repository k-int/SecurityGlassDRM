#!/bin/bash

curl -XPUT 'http://localhost:9200/media/owner/_mapping' -d '
{
    "work" : 
    {
        "properties" : 
        {
            "owner" : {"type":"string", "store":"yes","index":"not_analyzed"},
            "identifier" : {"type":"string","store":"yes","index":"not_analyzed"}
        }
    }
}
'

