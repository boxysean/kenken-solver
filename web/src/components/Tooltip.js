import React from 'react';
import { CSSTransition } from 'react-transition-group';
import classNames from 'classnames/bind';
import styles from './Tooltip.css';


class Tooltip extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    var tooltipStyle = {
      backgroundColor: this.props.color,
    };

    return (
      <CSSTransition
        in={this.props.showTooltip}
        timeout={500}
        classNames="Tooltip"
        unmountOnExit
      >
        <div
          onMouseDown={(event) => this.props.touchTooltip()}
          onTouchStart={(event) => this.props.touchTooltip()}
          className="TooltipContainer"
        >
          <div className="Tooltip" style={tooltipStyle}>
            {this.props.children}
          </div>
        </div>
      </CSSTransition>
    );
  }
}

export default Tooltip;
