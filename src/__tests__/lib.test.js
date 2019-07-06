import * as React from 'react';
import Adapter from 'enzyme-adapter-react-16';
import { shallow, configure } from 'enzyme';
import toJson, { createSerializer } from 'enzyme-to-json';
import { ExampleComponent } from '../../example/ExampleComponent';

expect.addSnapshotSerializer(createSerializer({ mode: 'deep' }));

configure({ adapter: new Adapter() });

describe('React Native Tapjoy', () => {
  test('renders with basic hooks', () => {
    const wrapper = shallow(<ExampleComponent />);

    expect(toJson(wrapper)).toMatchSnapshot();
  });
});
