import React from 'react';
import Button from 'react-pushy-buttons';
import styles from './SolveButton.css';
import 'react-pushy-buttons/css/pushy-buttons.css'
import classNames from 'classnames/bind';

let cx = classNames.bind(styles);

class SolveButton extends React.Component {
  render() {
    let className = cx({
      ButtonWrapper: true,
      ButtonWrapperDisabled: !this.props.canSubmit,
    });

    return (
      <div className={className}>
        <Button
          size="lg"
          color="green"
          onClick={this.props.onSubmit}
          disabled={!this.props.canSubmit}
        >
          Solve!
        </Button>
      </div>
    );
  }
}

export default SolveButton;
