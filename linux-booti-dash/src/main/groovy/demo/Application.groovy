package demo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import php.java.servlet.ContextLoaderListener
import php.java.servlet.PhpCGIFilter
import php.java.servlet.PhpJavaServlet
import php.java.servlet.fastcgi.FastCGIServlet

import javax.servlet.Filter
import javax.servlet.ServletContextListener

@Configuration
@ComponentScan
@EnableAutoConfiguration
class Application {

    static void main(String[] args) {
        SpringApplication.run Application, args
    }

    @Bean
    public ServletRegistrationBean phpJavaServlet() {
        return new ServletRegistrationBean(new PhpJavaServlet(), "*.phpjavabridge");
    }

    @Bean
    public ServletRegistrationBean cgiServlet() {
        return new ServletRegistrationBean(new FastCGIServlet(), "*.php");
    }

    @Bean
    public Filter phpCGIFilter() {
        return new PhpCGIFilter();
    }

    @Bean
    public ServletContextListener contextListener() {
        return new ContextLoaderListener();
    }
}