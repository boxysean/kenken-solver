import React from 'react';
import Button from 'react-pushy-buttons';
import styles from './SubmitButton.css';
import 'react-pushy-buttons/css/pushy-buttons.css'
import classNames from 'classnames/bind';

let cx = classNames.bind(styles);

class SubmitButton extends React.Component {
  render() {
    let className = cx({
      ButtonWrapper: true,
      ButtonWrapperDisabled: !this.props.canSubmit,
    });

    return (
      <div className={className}>
        <Button
          size="lg"
          color="blue"
          onClick={this.props.onSubmit}
          disabled={!this.props.canSubmit}
        >
          Solve!
        </Button>
      </div>
    );
  }
}

export default SubmitButton;
