var React = require('react')
var API = require('./servicios/API')
var EventBus = require('./servicios/EventBus')

module.exports = React.createClass({
    verSignificado : function () {
      var pal=this.props.nombre;
       API.getSignificado(pal).then(function(datos){
        var nuevos=datos;
        EventBus.eventEmitter.emitEvent('nuevoSignificado', [nuevos])
       })
    },
    render: function () {
      if(this.props.tipo=='common') 
      {
        return <span> <a href="#" onClick={this.verSignificado}>{this.props.nombre}</a></span>
      }
      else if(this.props.tipo=='other')
      {
        return <br/>
      }
      else if(this.props.tipo=='first') 
      {
        return <a href="#" onClick={this.verSignificado}> {this.props.nombre}</a>
        
      }
      else if(this.props.tipo=='period')
      {
        return <span><label>{this.props.nombre}</label><br/></span>
      }
      else
      {
        return <label>{this.props.nombre}</label>
      }
    }
})

