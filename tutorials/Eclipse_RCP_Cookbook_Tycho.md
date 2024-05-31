# Eclipse RCP Cookbook – The Thermomix Recipe (Automated build with Maven Tycho)

In this recipe we add the ability to build the Eclipse application headless via Maven.

## Ingredients

This recipe is based on the [Eclipse RCP Cookbook – The Food Combining Recipe](Eclipse_RCP_Cookbook_Services_Events.md). To get started fast with this recipe, the recipe is prepared for you on GitHub .

To use the prepared recipe, import the project by cloning the Git repository:

- _File → Import → Git → Projects from Git_
- Click _Next_
- Select _Clone URI_
- Enter URI _https://github.com/fipro78/e4-cookbook-basic-recipe.git_
- Click _Next_
- Select the **service** branch
- Click _Next_
- Choose a directory where you want to store the checked out sources
- Click _Next_
- Select _Import existing Eclipse projects_
- Click _Next_
- Click _Finish_

Additionally you need [Maven](https://maven.apache.org/) installed in your system.
- [Download Maven >= 3.9.6](https://maven.apache.org/download.cgi)
- Follow the [Installation Instructions](https://maven.apache.org/install.html)

## Preparation

### Step 1: Enable Maven Tycho Extensions

To reduce the maintenance effort, we will use the _pom-less Tycho Extensions_. For this we need to add a Maven Extension Descriptor to our project.

- Create a new folder _.mvn_ in the root folder of the project
- Create a file _extensions.xml_ in that folder
- Edit the file and add the following content

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
  <extension>
    <groupId>org.eclipse.tycho</groupId>
    <artifactId>tycho-build</artifactId>
    <version>4.0.8</version>
  </extension>
</extensions>
```

### Step 2: Create the Build Descriptor

Now we need to provide the instructions on how to build our application. This is done via a [pom.xml](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#:~:text=What%20is%20a%20POM%3F,default%20values%20for%20most%20projects.) file.

- Create a file _pom.xml_ in the root folder of the project
- Edit the file and add the following content
  - In the `modules` section 
    - List all modules that should be included in the build. This actually means all folders inside the root folder of the project.  
    *__Note:__*  
  The best practice for projects with multiple plug-ins, features and tests is to have a _structured environment_. Pomless Tycho also supports such environments. For further information on this, have a look at the links at the bottom of this page.
  - In the `plugins` section 
    - Add the `org.eclipse.tycho:tycho-maven-plugin` with `extensions=true` to use the Maven Tycho Plugin for building the application
    - Add the `org.eclipse.tycho:target-platform-configuration` to use the Target Definition we have created and activated also in the Eclipse IDE. Add configurations to build for different `environments`
    
The pom.xml file should look similar to the following example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project
	xmlns="http://maven.apache.org/POM/4.0.0" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

	<modelVersion>4.0.0</modelVersion>

	<groupId>org.fipro.eclipse.tutorial</groupId>
	<artifactId>parent</artifactId>
	<version>1.0.0-SNAPSHOT</version>

	<packaging>pom</packaging>

	<name>Eclipse Cookbook Application</name>
	<description>A simple Eclipse RCP application created by a Getting Started Cookbook</description>

	<modules>
		<!-- The Target Platform -->
		<module>org.fipro.eclipse.tutorial.target</module>

		<!-- The Plug-in Projects -->
		<module>org.fipro.eclipse.tutorial.inverter</module>
		<module>org.fipro.eclipse.tutorial.service.inverter</module>
		<module>org.fipro.eclipse.tutorial.logview</module>
		<module>org.fipro.eclipse.tutorial.app</module>
		
		<!-- The Feature Projects -->
		<module>org.fipro.eclipse.tutorial.feature</module>
		
		<!-- The Release Engineering Projects -->
		<module>org.fipro.eclipse.tutorial.product</module>
	</modules>

	<properties>
		<tycho.version>4.0.8</tycho.version>
		<maven.compiler.source>17</maven.compiler.source>
		<maven.compiler.target>17</maven.compiler.target>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	</properties>
    
	<build>
		<plugins>
			<plugin>
				<groupId>org.eclipse.tycho</groupId>
				<artifactId>tycho-maven-plugin</artifactId>
				<version>${tycho.version}</version>
				<extensions>true</extensions>
			</plugin>
			<plugin>
				<groupId>org.eclipse.tycho</groupId>
				<artifactId>target-platform-configuration</artifactId>
				<version>${tycho.version}</version>
				<configuration>
					<target>
						<artifact>
							<groupId>org.fipro.eclipse.tutorial</groupId>
							<artifactId>org.fipro.eclipse.tutorial.target</artifactId>
							<version>${project.version}</version>
						</artifact>
					</target>
					<targetDefinitionIncludeSource>honor</targetDefinitionIncludeSource>
					<executionEnvironment>JavaSE-17</executionEnvironment>
					<environments>
						<environment>
							<os>win32</os>
							<ws>win32</ws>
							<arch>x86_64</arch>
						</environment>
						<environment>
							<os>linux</os>
							<ws>gtk</ws>
							<arch>x86_64</arch>
						</environment>
						<environment>
							<os>macosx</os>
							<ws>cocoa</ws>
							<arch>x86_64</arch>
						</environment>
					</environments>
				</configuration>
			</plugin>
		</plugins>
		
		<!-- 
			Add this to avoid the warning: 
			'build.plugins.plugin.version' for org.eclipse.tycho:tycho-p2-director-plugin is missing. 
		-->
		<pluginManagement>
			<plugins>
				<plugin>
					<groupId>org.eclipse.tycho</groupId>
					<artifactId>tycho-p2-director-plugin</artifactId>
					<version>${tycho.version}</version>
				</plugin>
			</plugins>
		</pluginManagement>
	</build>
</project>
```

### Step 3: Cook

- Open a console
  - Option A: the console / terminal of your OS
  - Option B: the _Terminal_ view of the Eclipse IDE
    - _Right click on one of the projects → Show in Local Terminal → Terminal_
- Ensure you are in the root folder of the project
- Execute the following command
  ```
  mvn clean verify
  ```

### Step 4: Taste

After a successfull build, the build result can be found _org.fipro.eclipse.tutorial.product/target/products_. That folder contains archive files for the configured environments, and the extracted environment specific subfolders in _org.fipro.eclipse.tutorial_.

- Switch to the subfolder for your environment
- Execute the native launcher, e.g. 
  - Linux GTK - _org.fipro.eclipse.tutorial.product/target/products/org.fipro.eclipse.tutorial/linux/gtk/x86_64/eclipse_
  - Mac OS - _org.fipro.eclipse.tutorial.product/target/products/org.fipro.eclipse.tutorial/macosx/cocoa/x86_64/Eclipse.app/Contents/MacOS/eclipse_
  - Windows - _org.fipro.eclipse.tutorial.product/target/products/org.fipro.eclipse.tutorial/win32/win32/x86_64/eclipse.exe_

  __*Note:*__  
  If you get the following error on Windows

  ```
  The Eclipse executable launcher was unable to locate its companion shared library.
  ```
  you probably face the _Windows long path issue_. In this case extract the product archive to a folder with less depth in the file system and try again.



Further information:
 - [Tycho Advanced by Dirk Fauth](https://vogella.com/blog/tycho-advanced/)
 - [POM-less Tycho builds for structured environments](https://vogella.com/blog/pom-less-tycho-builds-for-structured-environments/)
 - [POM-less Tycho enhanced](https://vogella.com/blog/pom-less-tycho-enhanced/)
 - [Tycho Wiki - Documentation](https://github.com/eclipse-tycho/tycho/wiki)
 - [Tycho Wiki - Tycho Pomless](https://github.com/eclipse-tycho/tycho/wiki/Tycho-Pomless)