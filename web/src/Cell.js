import React from 'react';

import classNames from 'classnames/bind';

import styles from './Cell.css';

let cx = classNames.bind(styles);


class Cell extends React.Component {
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
        {this.props.cellIndex}
      </div>
    );
  }
}

export default Cell;
