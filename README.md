# CARD BINDING GENERAL SNAP

Microservice for General Binding Card Snap

## Version & Author History

* v1.0.1 - Susilo(Susilo.Rahmadi@bni.co.id)
  * Fixed authCode code and add trigger PhoneNo

* v1.0.0 - Susilo(Susilo.Rahmadi@bni.co.id)
    * BNI Card Binding And Inquiry

## Getting Started

### Deployment Cluster

* Openshift
* https://api.dev-bpsoa.ocp.hq.bni.co.id:6443
* Namespace : 

### Image Registry

* Harbor
* https://jtl-tkgiharbor.hq.bni.co.id/
* Project : <ns>
* Pull Image
```
docker pull 
docker tag
docker push
```


### Installing

* login cluster
* switch namespace to <ns>
* apply yaml file

### Executing program

* login cluster
```
oc login https://api.dev-bpsoa.ocp.hq.bni.co.id:6443 --username=$USERNAME --password=$PASSWORD
```
* select namespace <ns>
```
oc project <ns>
```
* apply yaml file
```
oc apply
```

## Help

Any advise for common problems or issues.
```
Microsoft Teams @PSD
```



## License



## Acknowledgments

