# SETL
SETL is a data integration tool. It provides a unified platform for processing and integrating data semantically by bridging Semantic Web and BI technologies. SETL allows to create a semantic Data Warehouse (SDW), a repository of semantically integrated data by combining different data sources. We divide the whole integration process into three layers: Definition Layers (DL), ETL Layers (ETLL) and OLAP Layers (OL). The output of one layer is the input of the immediate next layer. In DL, user can define 1) the target with MD semantics using OWL, QB and QB4OLAP constructs and 2) the mappings between source and target. constructs. ETLL is composed of a set of operators which are used to create ETL flows to create a SDW. OL provides an interface where user can create their analytical query by selecting different levels of hierarchies and measures.
## How to run
In order to run the project, you need to ***clone the project*** and ***run the SETLFrame.java*** under **view** package.

## Video link
Here, we describe how to use SETL using a use case.. https://www.youtube.com/watch?v=9-a4MVHqZow



## Description with examples
In the Example folder, you find some sample files which we use to demonstrate a part of our use case. 

For **Target TBox Definer**, you may use either **populationSourceTBox_01.ttl** or **PopulationByResAdm5LivposTargetTBox-1.ttl** from ./Example_ folder to edit, update, add or delete components from an existing Tbox. Here, **PopulationByResAdm5LivposTargetTBox-1.ttl** is the target TBox of our use case and **populationSourceTBox_01.ttl** is the source TBox  

For ***Source2TargetMapping Definer***, you may use the **map_version_new_1560871745299.ttl** file as _Mapping File_, **populationSourceTBox_01.ttl** as _Source TBox_ and **PopulationByResAdm5LivposTargetTBox-1.ttl** as _Target TBox_. You will find these demo files in the _example_ folder.

For ***ETL Layer***, you may use the given _ETL FLow_ file in the _example_ folder under the name **ETL_Flow_190912_120650.xml**.

For ***OLAP Layer***, you may use the **PopulationByResAdm5LivposTargetTBox-1.ttl** as TBox and **demo_TargetABox.ttl** as ABox from the _example_ folder for Local File OLap Operation. Now you can create your own OLAP queries by interacting with GUI components and get the desired result. 


## Package description
In the Package/Project Explorer tab, you will find 10 packages under the project. A short description of the packages is given below-
##### 1. "controller" package
The **controller** package as the name implies works like the _Controller_ in an **MVC** pattern. It holds the response of the _sparql_ queries and also acts on _user's_ input, process the result and passes the results back to the **View**.
##### 2. "core" package
The **core** package contains some core functions of the ***SETL***. It contains the methods for operations _i.e._ **Level Entry Generator, Fact Entry Generator, ABox to TBox Deriver, Transformation on Literal, Non Semantic to TBox Deriver, Update Dimensional Construct, RDF Wrapper** and **Instance Entry Generator**.
##### 3. "etl" package
The **etl** package has the methods required to save an _etl flow_ in _xml_ format and also parse the xml to restore the _etl flow_.
##### 4. "etl_model" package
The **etl_model** package contains the model classes for all the **ETL** operations.
##### 5. "helper" package
The **helper** package contains some common methods used all over the project and also some custom rendering class for _JList_ and _JTree_. It also contains the methods for _postgres_ db connection methods etc.
##### 6. "images" package
The **images** package contains the icons used in the project.
##### 7. "model" package
The **model** package serves the same purpose of the **Model** in **MVC** pattern. It contains the _pojo_ classes required in the project.
##### 8. "practice" package
The **practice** package contains the methods which were created for practice before finalizing the methods. These methods are not being used in the main project. Hence, deletion of this project will now affect the main **SETL** project.
##### 9. "queries" package
The **queries** package contains methods that incorporates the _sparql_ queries for retrieving data from input files. These methods are used by the **Controller** to fetch the results from the files and present it in the **View**.
##### 10. "view" package
The **view** package contains all the panels and frames used in this project. Results from **controller** are being displayed here.



## Contact 
If you have any queries contact
rudra@cs.aau.dk 
amritcsecu@gmail.com
