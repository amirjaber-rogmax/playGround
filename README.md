# playGround
University Project Google Maps API 2 and CRUD

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

For this project you will need android studio and a server (could be local or you could use a free one such as 000webhost)

### Installing

To install, run Android Studio and clone the repository to have the project locally

![git clone](https://snag.gy/Cb4wUr.jpg)

```
git clone https://github.com/amirjaber-rogmax/playGround.git
```

Then make sure that all the gradle files are upto date and fix all missing dependencies

```
make sure you check the sdk version and the distributionUrl in gradle-wrapper.properties
```
And you should end up with your project looking like this, without any build errors

![project files](https://snag.gy/6xBY9n.jpg)

After that you need to create your own <a href="https://console.developers.google.com/apis/credentials" target="blank">google api</a>

then in Android studio get the package name 

```
com.rogmax.amirjaber.playground
```
and the SHA-1 certificate fingerprint

![build3](https://user-images.githubusercontent.com/31887286/37705072-77e2fd74-2d03-11e8-8677-9bd113db6187.gif)
![build4](https://user-images.githubusercontent.com/31887286/37705106-91da81d4-2d03-11e8-882c-deef74f62924.gif)

From here, you wanna create new credentials and get your api key, 

![Api key](https://snag.gy/7QaGAn.jpg)

then in the project's manifest file you replace my api key with yours

![My Key](https://snag.gy/fIkeEV.jpg)

Now your Application is ready and good to run on your mobile, but did you forget about the server side?
Download the server side zip from the link at the end of this readMe, then upload to a host (local or server).
Then in Android studio, change the set address to the one you uploaded it to.

![API URL](https://snag.gy/EANqyw.jpg)

and aswell as the places where i check for connection

![Test Con](https://snag.gy/6gcCKZ.jpg)

<a href="https://drive.google.com/open?id=1lJ4Hj-lZ5yCAQG8SAM0pahAUYAFun_Ou" target="blank">Download Server</a>
<a href="https://drive.google.com/open?id=1rarJh_9zm8a3iLM2OLeSp8kwjwRZL0jU" target="blank">Download APK</a>

## Authors

* **Amir Jaber** - *Initial work* 

