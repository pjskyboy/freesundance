import com.google.appengine.api.datastore.Entity

Entity entity = new Entity("person")

// subscript notation, like when accessing a map
entity['name'] = "Guillaume Laforge"

// normal property access notation
entity.age = 32

log.info "entity.name [" + entity.name + "] entity.age [" + entity.age + "]"

entity.save()


forward "/index.gtpl"