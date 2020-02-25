# linear-config
Configuration module of the **Linear Framework**.

## API
The Configuration API allows for configuration settings to be loaded from a properties file.

### Quick Demo
```scala
import com.linearframework.config._

val conf = Configuration.load()

val dbUrl = conf.getString("db.url")
val updateFlag = conf.getBoolean("db.auto-update")
val port = conf.getInt("server.port", default = 8080)
```


### load()
This call checks the JVM argument `config.file`.
 - If `config.file` is defined, the properties in that file are loaded into a `Configuration` object.
 - If `config.file` is not defined, the properties from `application.properties` are loaded into a `Configuration` object.

To check a different JVM argument and/or use a different default Properties file, the following are available:
```scala
Configuration.load(<jvm_arg>, <properties_file>)
Configuration.load(<properties_file>)
```

Properties files provided to `Configuration.load()` may either be on the classpath or on the external file system.
For external files, an absolute path is required.


### getXYZ()
The `getString()`, `getInt()`, `getLong()`, `getDouble()`, and `getBoolean()` calls have similar signatures:
 - `getString("server.host")`
   - Returns the `server.host` property
   - If `server.host` is not defined, throws a `PropertyNotFoundException`
 - `getString("server.host", "localhost")`
   - Returns the `server.host` property
   - If `server.host` is not defined, returns "localhost"


### Property Overrides
Configuration properties can be overridden in two ways:
 1. Environment Variables
    - Setting the environment variable `server.host=dev.myproduct.com` will override the value of `server.host` in the properties file 
      with "dev.myproduct.com"
 1. JVM arguments
    - Setting `-Dserver.host=stage.myproduct.com` will override the value of `server.host` in the properties file 
      with "stage.myproduct.com"

JVM arguments take precendence over environment variables.  That is, If `server.host` is defined both as an environment variable and 
a JVM argument, the JVM argument will be used and the environment variable will be ignored.