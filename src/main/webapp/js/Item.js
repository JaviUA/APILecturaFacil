var React = require('react')

module.exports = React.createClass({
    verSignificado : function (evento) {
       this.props.handleVerDetalles(this.props.pos)
    },
    render: function () {
        return  <label for="palabra">{this.props.nombre}</label>
    }
})