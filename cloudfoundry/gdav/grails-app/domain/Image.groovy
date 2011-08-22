class Image {
    byte[] data
    String contentType
    String name
    Date dateCreated
    Date lastUpdated

    static constraints = {
        data(nullable:true)
        contentType(nullable:true)
    }
}
