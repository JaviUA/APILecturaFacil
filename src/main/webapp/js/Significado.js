var React = require('react')
var API = require('./servicios/API')
var EventBus = require('./servicios/EventBus')

module.exports = React.createClass({
    render: function () {
        return <span> {this.props.texto} <br/></span>
    }
})
