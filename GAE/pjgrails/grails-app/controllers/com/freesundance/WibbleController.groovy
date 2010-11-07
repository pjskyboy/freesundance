package com.freesundance

import com.google.appengine.api.datastore.*
class WibbleController {

	def persistenceManager
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
		def query = persistenceManager.newQuery( Wibble )
		def  wibbleInstanceList = query.execute()
		def total = 0
		if(  wibbleInstanceList &&  wibbleInstanceList.size() > 0){
			total =  wibbleInstanceList.size()
		}
		[  wibbleInstanceList :  wibbleInstanceList,  wibbleInstanceTotal: total ]
    }

    def show = {
	    def wibbleInstance = persistenceManager.getObjectById( Wibble.class, Long.parseLong( params.id )  )
        if(!wibbleInstance) {
            flash.message = "Wibble not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ wibbleInstance : wibbleInstance ] }
    }

    def delete = {
	    def wibbleInstance = persistenceManager.getObjectById( Wibble.class, Long.parseLong( params.id )  )
        if(wibbleInstance) {
            try {
                persistenceManager.deletePersistent(wibbleInstance)
                flash.message = "Wibble ${params.id} deleted"
                redirect(action:list)
            }
            catch(Exception e) {
                flash.message = "Wibble ${params.id} could not be deleted"
                redirect(action:show,id:params.id)
            }
        }
        else {
            flash.message = "Wibble not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
	    def wibbleInstance = persistenceManager.getObjectById( Wibble.class, Long.parseLong( params.id )  )
		if(!wibbleInstance) {
            flash.message = "Wibble not found with id ${params.id}"
            redirect(action:list)
        }
        else {
			wibbleInstance = persistenceManager.detachCopy( wibbleInstance )    
        	return [ wibbleInstance : wibbleInstance ]
        }
    }

    def update = {
	 	def wibbleInstance = persistenceManager.getObjectById( Wibble.class, Long.parseLong( params.id )  )
    
    	if(wibbleInstance) {
            wibbleInstance.properties = params
            if(!wibbleInstance.hasErrors()){
	
				try{
					persistenceManager.makePersistent(wibbleInstance)
				} catch( Exception e ){
				   	render(view:'edit',model:[wibbleInstance:wibbleInstance])
				}finally{
					flash.message = "Wibble ${params.id} updated"
	                redirect(action:show,id:wibbleInstance.id)
				}        
 			}
            else {
                render(view:'edit',model:[wibbleInstance:wibbleInstance])
            }
        }
        else {
            flash.message = "Wibble not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def create = {
        def wibbleInstance = new Wibble()
        wibbleInstance.properties = params
        return ['wibbleInstance':wibbleInstance]
    }

    def save = {
        def wibbleInstance = new Wibble(params)
		if(!wibbleInstance.hasErrors() ) {
			try{
				persistenceManager.makePersistent(wibbleInstance)
			} finally{
				flash.message = "Wibble ${wibbleInstance.id} created"
				redirect(action:show,id:wibbleInstance.id)	
			}
		}
   
		render(view:'create',model:[wibbleInstance:wibbleInstance])
        
    }
}
