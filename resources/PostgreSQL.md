## PostgreSQL Migration Notes
Both PostgreSQL and MySQL are relational database management system based on relational model. They store data in 
tables, defining entities and relationships. RDBMSes also support complex query operations such as aggregations, 
filtering, and joins etc. It is well-suited for structured data when schema can be fixed in advance and complex query 
operations are needed. Therefore, RDBMSes may be a good fit to a trade capture system, managing trade records, 
instrument data, book positions etc and requiring inter-table joins.

### PostgreSQL vs MySQL
PostgreSQL is an object-relational database management system with an object-oriented database model, supporting 
objects, classes, and inheritance. It supports both SQL (relational) and JSON (non-relational) queries. It is ACID 
compliant and supports MVCC, a variety of indexes, and Object data types. It also offers advanced view options, stored 
procedure languages, and trigger conditions. PostgreSQL works better for write operations.

MySQL is a relational database management system more optimized for read operations. ACID compliance and MVCC support 
vary depending on the internal storage engine. For example, InnoDB supports both. In general, MySQL offers less features
compared with PostgreSQL.

#### Tradeoffs of using PostgreSQL
Pros: Support more flexible data types and complex queries, achieve better write performance (MVCC, without read-write 
locks), offer a larger variety of features in general.

Cons: Require memory-intensive resources to scale for multiple users thus less efficient in read operations, steeper 
learning curve to unleash the full potential of PostgreSQL.

#### References
- [AWS](https://aws.amazon.com/compare/the-difference-between-mysql-vs-postgresql/)