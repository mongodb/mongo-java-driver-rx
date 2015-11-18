+++
date = "2015-10-08T09:56:14Z"
title = "Changelog"
[menu.main]
  weight = 50
  pre = "<i class='fa fa-cog'></i>"
+++

## Changelog

Changes between released versions


### 1.2 

  * Updated MongoDB Driver Async to 3.2.0
    
  * Updated RxJava to 1.0.16
  
  * Added `ObservableAdapter` to allow for custom adapters for `Observable`, such as customizing Scheduler threads.

### 1.1 

  * Updated MongoDB Driver Async to 3.1.0
  
    Simplified the driver by using the new `com.mongodb.async.client.Observable` and mapping to `rx.Observable`
    
  * Updated RxJava to 1.0.14

