class Person {
    String name
    Date dateCreated
    Date lastUpdated

    static hasMany = [ images: Image]
}
