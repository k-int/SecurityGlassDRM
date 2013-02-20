#!/bin/bash

curl -XPUT 'http://mediatest.k-int.com/elasticSearch/media/owner/_mapping' -d '
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

