package com.freesundance



import javax.jdo.annotations.*;
// import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Wibble implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id

	@Persistent
	String name
	
	@Persistent
	String value

    static constraints = {
    	id( visible:false)
	}
}
