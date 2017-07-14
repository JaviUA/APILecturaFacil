var React = require('react')
var API = require('./servicios/API')
var EventBus = require('./servicios/EventBus')

module.exports = React.createClass({
    verSignificado : function () {
    
    },
    render: function () {
     
        return  <li className="list-group-item"> {this.props.nom}: {this.props.val} </li>
 
    }
})