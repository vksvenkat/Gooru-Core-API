Gooru Core API
==============
Gooru Core API project consists of APIs that required for Gooru Web this project is developed in Spring MVC framework


## Dependencies 
<table>
  <tr>
    <th style="text-align:left;">JDK</th>
    <td>1.7 or above</td>
  </tr>
  <tr>
    <th style="text-align:left;">Operating System</th>
    <td>Windows 7 and above or Ubuntu</td>
  </tr>
   <tr>
    <th style="text-align:left;">Application container</th>
    <td>Apache tomcat7</td>
  </tr>
   <tr>
    <th style="text-align:left;">Apache Maven</th>
    <td>Maven 3.0.4</td>
  </tr>
</table>

## Build
* Update your tomcat location in "webapp.container.home" property in root pom.xml
For example, `<webapp.container.home>${env.CATALINA_HOME}</webapp.container.home>`
* Navigate to the development project folder.
For example, cd Home\Projects\Gooru-Core-API 
* From the linux terminal Clean install the build.
Command: `mvn clean install -P api -Dmaven.test.skip=true`
* Project deployed on <webapp.container.home>/webapps/ location


## License
Gooru Core API is released under the MIT License. 
