import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*


def query = new Query("person")

// sort results by descending order of the creation date
//query.addSort("age", Query.SortDirection.DESCENDING)

// filters the entities so as to return only scripts by a certain author
query.addFilter("name", Query.FilterOperator.EQUAL, "Peter Jupp")

PreparedQuery preparedQuery = datastore.prepare(query)

// return only the first 10 results
def entities = preparedQuery.asList( withLimit(10) )

for (Entity entity : entities) {
	log.info("name: " + entity.name + " age: " + entity.age)
}

Entity entity1 = new Entity("person")
// subscript notation, like when accessing a map
entity1['name'] = "Guillaume Laforge"
// normal property access notation
entity1.age = 32
log.info "entity1.name [" + entity1.name + "] entity1.age [" + entity1.age + "]"
entity1.save()

Entity entity2 = new Entity("person")
// subscript notation, like when accessing a map
entity2['name'] = "Peter Jupp"
log.info "entity2.name [" + entity2.name + "] entity2.age [" + entity2.age + "]"
entity2.save()

forward "/index.gtpl"