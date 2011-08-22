import grails.util.GrailsUtil

class BootStrap {

    def init = {servletContext ->


        new Folder(name:"Top1").addToFiles(readfile('sa400023.jpg'))
                .addToFolders( new Folder(name:"Sub1").addToFiles(readfile('sa400024.jpg')))
                .addToFolders( new Folder(name:"Sub2").addToFiles(readfile('sa400025.jpg'))
                    .addToFolders(new Folder(name:"Sub2Sub1").addToFiles(readfile('sa400026.jpg')))
                ).save()                    

        def role = new Role(authority:"ROLE_WEBDAV", description:"WEBDAV users")
        assert role.save()

        def user = new User(username:'webdav', passwd:'webdav', userRealName:'a webdav user',
                enabled:true, email: 'webdav@nodomain.com')
        user.addToAuthorities(role)
        if( !user.save() ) {
            user.errors.each {
                log.error "user error: $it"
            }
            //assert false
        }

        
    }
    def destroy = {
    }

    private readfile(name) {
        [ name: name, data: new File("data/$name").readBytes()]
    }
} 