class Folder {

    String name
    Folder parent
    Date dateCreated
    Date lastUpdated

    static hasMany = [ files: FileObject, folders:Folder]

    static constraints = {
        parent(nullable:true)
    }

    String toString() {
        name
    }

}
