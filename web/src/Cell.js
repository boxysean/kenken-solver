import React from 'react';
import styles from './Cell.css';
import classNames from 'classnames/bind';

let cx = classNames.bind(styles);


class Cell extends React.Component {
  setConstraint(value, operator) {
    this.setState({
      constraint: value + operator,
    })
  }

  render() {
    let className = cx({
      Cell: true,
      isSelected: this.props.isSelected,
    });

    return (
      <div className={className}
        onMouseMove={(event) => this.props.processHover(this.props.cellIndex)}
        onMouseDown={(event) => this.props.processBegin(this.props.cellIndex)}
      >
        {this.props.constraint}
      </div>
    );
  }
}

export default Cell;
