class FileObject {

    byte[] data
    String name
    Date dateCreated
    Date lastUpdated

    static belongsTo = [folder:Folder]

    static constraints = {
        data(maxSize:1000000)
    }

    String toString() {
        name
    }

   
}
